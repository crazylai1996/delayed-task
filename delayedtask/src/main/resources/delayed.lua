local key = KEYS[1]
local minVal = ARGV[1]
local maxVal = ARGV[2]
local todolist = redis.call("ZRANGEBYSCORE", key, minVal, maxVal, "limit", 0, 1)
local todo = todolist[1]
if todo == nil then
	return nil
else
	redis.call("ZREM", key, todo)
	return todo
end