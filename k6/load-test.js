import http from 'k6/http';
import { check, fail, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const PROFILE = __ENV.PROFILE || 'load';
const RUN_ID = __ENV.RUN_ID || `${Date.now()}-${Math.floor(Math.random() * 100000)}`;

const profiles = {
  smoke: {
    executor: 'constant-vus',
    vus: 1,
    duration: '30s',
  },
  load: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '30s', target: 10 },
      { duration: '1m', target: 30 },
      { duration: '3m', target: 50 },
      { duration: '1m', target: 0 },
    ],
    gracefulRampDown: '30s',
  },
  stress: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '30s', target: 10 },
      { duration: '1m', target: 50 },
      { duration: '2m', target: 100 },
      { duration: '1m', target: 100 },
      { duration: '1m', target: 0 },
    ],
    gracefulRampDown: '30s',
  },
  spike: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '30s', target: 10 },
      { duration: '10s', target: 100 },
      { duration: '30s', target: 100 },
      { duration: '10s', target: 10 },
      { duration: '30s', target: 0 },
    ],
    gracefulRampDown: '10s',
  },
  soak: {
    executor: 'constant-vus',
    vus: 30,
    duration: '30m',
  },
};

if (!profiles[PROFILE]) {
  throw new Error(`지원하지 않는 PROFILE입니다: ${PROFILE}`);
}

export const options = {
  // 로그인 세션 쿠키를 VU의 반복 실행 사이에도 유지한다.
  noCookiesReset: true,
  scenarios: {
    roommade: profiles[PROFILE],
  },
  thresholds: {
    'http_req_failed{type:business}': ['rate<0.01'],
    'http_req_duration{type:business}': ['p(95)<500'],
    checks: ['rate>0.99'],
  },
};

const jsonHeaders = {
  'Content-Type': 'application/json',
};

const readRequests = [
  { path: '/api/coins/balance', name: 'GET /api/coins/balance' },
  { path: '/api/preparations/readiness', name: 'GET /api/preparations/readiness' },
  { path: '/api/preparations/deposit', name: 'GET /api/preparations/deposit' },
  { path: '/api/living/emergency-funds', name: 'GET /api/living/emergency-funds' },
  { path: '/api/living/rir', name: 'GET /api/living/rir' },
  { path: '/api/rooms', name: 'GET /api/rooms' },
  { path: '/api/rooms/shop/furniture', name: 'GET /api/rooms/shop/furniture' },
  { path: '/api/youth-policies?page=1&size=10', name: 'GET /api/youth-policies' },
];

let initialized = false;
let credentials;

function requestParams(type, name) {
  return {
    headers: jsonHeaders,
    tags: { type, name },
  };
}

function apiSucceeded(response) {
  if (response.status < 200 || response.status >= 300) {
    return false;
  }

  try {
    return response.json('success') === true;
  } catch (error) {
    return false;
  }
}

function requireSuccess(response, operation, expectedStatus) {
  const passed = check(response, {
    [`${operation}: HTTP ${expectedStatus}`]: (result) => result.status === expectedStatus,
    [`${operation}: API success`]: apiSucceeded,
  });

  if (!passed) {
    fail(`${operation} 실패: status=${response.status}, body=${response.body}`);
  }
}

function initializeVu() {
  const email = `loadtest.${RUN_ID}.vu${__VU}@example.com`;
  const password = 'LoadTest123!';
  credentials = { email, password };

  const signupResponse = http.post(
    `${BASE_URL}/api/users/signup`,
    JSON.stringify({
      email,
      password,
      name: `부하테스트${__VU}`,
      birthDate: '2000-01-01',
      monthlyIncome: 2500000,
      workplaceRoadAddress: '서울특별시 중구 세종대로 110',
      workplaceDetailAddress: '부하테스트',
      depositLimit: 10000000,
      monthlyRentLimit: 500000,
    }),
    requestParams('setup', 'POST /api/users/signup'),
  );
  requireSuccess(signupResponse, '회원가입', 201);

  login('setup');

  const rentResponse = http.put(
    `${BASE_URL}/api/living/rent`,
    JSON.stringify({ monthlyRent: 400000 }),
    requestParams('setup', 'PUT /api/living/rent'),
  );
  requireSuccess(rentResponse, '월세 초기화', 200);

  const emergencyFundResponse = http.put(
    `${BASE_URL}/api/living/emergency-funds/target`,
    JSON.stringify({ targetAmount: 3000000 }),
    requestParams('setup', 'PUT /api/living/emergency-funds/target'),
  );
  requireSuccess(emergencyFundResponse, '비상금 목표 초기화', 200);

  initialized = true;
}

function login(type = 'business') {
  const response = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify(credentials),
    requestParams(type, 'POST /api/users/login'),
  );
  requireSuccess(response, '로그인', 200);
}

function runReadRequest() {
  const target = readRequests[Math.floor(Math.random() * readRequests.length)];
  const response = http.get(
    `${BASE_URL}${target.path}`,
    requestParams('business', target.name),
  );
  requireSuccess(response, target.name, 200);
}

function runWriteRequest() {
  if (Math.random() < 0.5) {
    const monthlyRent = 350000 + (__ITER % 6) * 10000;
    const response = http.put(
      `${BASE_URL}/api/living/rent`,
      JSON.stringify({ monthlyRent }),
      requestParams('business', 'PUT /api/living/rent'),
    );
    requireSuccess(response, '월세 수정', 200);
    return;
  }

  const targetAmount = 3000000 + (__ITER % 6) * 100000;
  const response = http.put(
    `${BASE_URL}/api/living/emergency-funds/target`,
    JSON.stringify({ targetAmount }),
    requestParams('business', 'PUT /api/living/emergency-funds/target'),
  );
  requireSuccess(response, '비상금 목표 수정', 200);
}

export default function () {
  if (!initialized) {
    initializeVu();
  }

  const trafficSelector = Math.random();
  if (trafficSelector < 0.1) {
    login();
  } else if (trafficSelector < 0.8) {
    runReadRequest();
  } else {
    runWriteRequest();
  }

  sleep(0.5 + Math.random());
}
