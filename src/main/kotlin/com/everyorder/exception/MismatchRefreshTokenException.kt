package com.everyorder.exception

class MismatchRefreshTokenException : BusinessException(ErrorCode.INVALID_JWT, "DB에 저장되어있지 않은 리프레시 토큰") {
}
