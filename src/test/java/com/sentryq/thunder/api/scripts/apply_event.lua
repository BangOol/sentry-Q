-- KEYS[1]: stockKey (e.g., stock:{event:1}) - Hash Tag 적용 필수
-- KEYS[2]: applySetKey (e.g., apply:users:{event:1})
-- ARGV[1]: userId

-- 1. 중복 검사 (이미 신청한 유저인가?)
-- SISMEMBER는 O(1)이므로 빠릅니다.
if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
    return -2 -- Code: Already Applied (중복)
end

-- 2. 재고 검사
-- tonumber()를 사용하여 문자열을 숫자로 변환합니다.
-- Redis의 GET은 값이 없으면 nil(false)을 반환하므로 or '0' 처리합니다.
local stock = tonumber(redis.call('GET', KEYS[1]) or '0')

if stock <= 0 then
    return -1 -- Code: Sold Out (품절)
end

-- 3. 재고 차감 및 유저 등록 (원자적 실행)
-- 여기까지 도달했다면 무조건 성공입니다.
redis.call('DECR', KEYS[1])
redis.call('SADD', KEYS[2], ARGV[1])

return 0 -- Code: Success (성공)