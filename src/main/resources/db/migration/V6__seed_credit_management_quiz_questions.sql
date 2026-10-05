/* =========================================================
   신용관리 OX 16문항 시드

   우리금융캐피탈 신용관리지침과 서민금융진흥원 안내를 참고해,
   원문을 복제하지 않은 RoomMade 자체 문항으로 작성한다.
   ========================================================= */

SET @credit_quiz_seed_json = JSON_ARRAY(
    JSON_OBJECT(
        'question', '신용카드 한도가 남아 있다면, 그 금액은 이번 달 가계 예산에 포함된 현금과 동일하게 봐도 된다.',
        'explanation', '신용카드 한도는 내 돈이 아니라 카드사가 빌려주는 한도예요. 결제일에 갚아야 할 채무가 될 수 있으므로 상환 계획 안에서 사용해야 해요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '월 납입액 부담을 줄이기 위해 대출 기간을 늘릴 때는, 월 납입액만 보고 결정해도 된다.',
        'explanation', '기간이 길어지면 월 납입액 부담은 줄 수 있지만, 전체 이자 부담은 커질 수 있어요. 소득과 상환 능력에 맞는 기간을 선택해야 해요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '내 신용정보를 확인할 때는 진행 중인 거래뿐 아니라 끝난 거래의 종료 사실도 제대로 반영됐는지 살펴볼 필요가 있다.',
        'explanation', '내 금융거래 정보가 정확하게 등록되고 종료된 거래가 제대로 반영됐는지 확인하면, 예상하지 못한 거래나 오류를 빠르게 발견하는 데 도움이 돼요.',
        'answer', 'O'
    ),
    JSON_OBJECT(
        'question', '소액 결제를 며칠 늦게 냈더라도, 이런 연체가 반복되면 신용관리에 영향을 줄 수 있다.',
        'explanation', '소액이라도 연체가 반복되거나 길어지면 신용도에 불리하게 작용할 수 있어요. 납부일을 관리하고 자동이체를 활용하는 것이 좋아요.',
        'answer', 'O'
    ),
    JSON_OBJECT(
        'question', '신용관리를 잘해도 그 이점은 대출을 받을 때에만 나타난다.',
        'explanation', '신용도는 대출뿐 아니라 카드 발급이나 여러 금융상품 이용 조건에도 영향을 줄 수 있어요. 다만 실제 조건은 소득과 부채 등 여러 요소를 함께 고려해 결정돼요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '카드 결제대금 상환이 부담스러워졌다면, 새 카드를 발급받기보다 현재 카드 사용액과 상환 계획부터 점검하는 것이 좋다.',
        'explanation', '결제 부담이 커졌다면 먼저 지출과 카드 사용을 점검해야 해요. 추가 사용을 늘리기보다 상환 계획을 세우고 재정 균형을 회복하는 것이 중요해요.',
        'answer', 'O'
    ),
    JSON_OBJECT(
        'question', '신용점수를 올려주겠다며 수수료나 선입금을 요구받았다면, 정식 금융회사일 가능성이 높다.',
        'explanation', '신용점수 상향 수수료, 공탁금, 기존 대출 상환금 등을 먼저 요구하는 연락은 대출 사기일 수 있어요. 돈을 보내지 말고 공식 창구로 확인하세요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '대출 심사에서는 연체 이력·소득·부채보다 신용점수 하나가 항상 우선한다.',
        'explanation', '대출 심사에서는 신용점수뿐 아니라 연체 이력, 소득, 부채, 상품 조건 등 여러 요소를 함께 확인해요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '신용점수만 높다면 대출과 카드 사용 내역을 따로 확인하지 않아도 내 금융 상태를 충분히 알 수 있다.',
        'explanation', '신용점수만 보지 않고 대출과 카드 사용 등 부채 현황도 함께 살피면, 상환 계획과 소비 습관을 점검하는 데 도움이 돼요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '대출 상담 문자에 적힌 번호가 공식 번호와 달라도 조건이 좋다면 먼저 송금해 확인해도 된다.',
        'explanation', '문자나 메신저로 대출을 미끼로 돈을 요구하면 사기를 의심해야 해요. 해당 금융회사 공식 연락처나 서민금융콜센터 1397로 확인하세요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '무료 본인 신용정보 열람 서비스를 이용해 내 신용점수를 여러 번 확인하면, 그 조회 횟수만으로 신용점수가 낮아진다.',
        'explanation', '본인이 자신의 신용정보를 확인하는 것은 신용관리를 위한 행동이에요. 단순 본인 조회만으로 신용점수가 오르거나 내려가지는 않아요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '대출을 모두 갚거나 카드를 해지한 뒤에는, 해당 거래의 종료 사실이 신용정보에 반영됐는지 확인할 필요가 없다.',
        'explanation', '거래가 종료된 뒤에도 신용정보에 정확히 반영됐는지 확인하는 것이 좋아요. 내 금융거래 정보의 오류나 예상하지 못한 내역을 발견하는 데 도움이 돼요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '같은 신용점수라도 금융기관의 자체 기준과 거래 이력에 따라 대출 조건이 달라질 수 있다.',
        'explanation', '대출 조건은 신용점수만으로 정해지지 않아요. 금융기관과 상품별 심사 기준, 소득·부채 현황, 거래 이력 등을 함께 고려해 달라질 수 있어요.',
        'answer', 'O'
    ),
    JSON_OBJECT(
        'question', '신용카드 혜택을 바꾸고 싶다면 짧은 기간에 여러 장을 발급받아도, 발급 이력이나 카드 보유 기간은 신용평가와 전혀 관련이 없다.',
        'explanation', '카드 발급과 보유 기간 등은 신용평가에 활용될 수 있는 거래 정보예요. 혜택만 보고 짧은 기간에 여러 장을 발급받기보다 필요한 카드인지 먼저 살펴보는 것이 좋아요.',
        'answer', 'X'
    ),
    JSON_OBJECT(
        'question', '대출이나 카드 발급을 앞두고 금융기관에서 내 명의로 발생한 신용조회가 내가 요청한 것인지 확인할 필요가 있다.',
        'explanation', '금융기관은 대출이나 카드 발급 심사 과정에서 신용조회를 할 수 있어요. 내가 요청한 조회인지 확인하면 명의도용이나 예상하지 못한 거래를 점검하는 데 도움이 돼요.',
        'answer', 'O'
    ),
    JSON_OBJECT(
        'question', '카드대금과 대출이자를 자동이체로 설정했다면, 결제일 전에 계좌 잔액을 확인하지 않아도 연체를 막을 수 있다.',
        'explanation', '자동이체는 납부일을 놓치는 실수를 줄여주지만, 계좌 잔액이 부족하면 연체될 수 있어요. 결제일 전 잔액을 함께 확인하는 습관이 필요해요.',
        'answer', 'X'
    )
);

INSERT INTO quiz_questions (
    question,
    explanation,
    quiz_type,
    active
)
SELECT
    seed.question,
    seed.explanation,
    'OX',
    TRUE
FROM JSON_TABLE(
    @credit_quiz_seed_json,
    '$[*]' COLUMNS (
        question TEXT PATH '$.question',
        explanation TEXT PATH '$.explanation',
        answer CHAR(1) PATH '$.answer'
    )
) AS seed;

SET @first_credit_quiz_question_id = LAST_INSERT_ID();

INSERT INTO quiz_choices (
    question_id,
    content,
    is_correct
)
SELECT
    @first_credit_quiz_question_id + seed.ordinality - 1,
    'O',
    seed.answer = 'O'
FROM JSON_TABLE(
    @credit_quiz_seed_json,
    '$[*]' COLUMNS (
        ordinality FOR ORDINALITY,
        answer CHAR(1) PATH '$.answer'
    )
) AS seed
UNION ALL
SELECT
    @first_credit_quiz_question_id + seed.ordinality - 1,
    'X',
    seed.answer = 'X'
FROM JSON_TABLE(
    @credit_quiz_seed_json,
    '$[*]' COLUMNS (
        ordinality FOR ORDINALITY,
        answer CHAR(1) PATH '$.answer'
    )
) AS seed;

SET @credit_quiz_seed_json = NULL;
SET @first_credit_quiz_question_id = NULL;
