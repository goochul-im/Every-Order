package com.everyorder.exception

class InvalidRefreshTokenException : BusinessException(ErrorCode.INVALID_JWT, "인증되지 않은 리프레시 토큰") {
}
