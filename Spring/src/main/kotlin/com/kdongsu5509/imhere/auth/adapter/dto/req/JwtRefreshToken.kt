import jakarta.validation.constraints.NotBlank

data class JwtRefreshToken(
    @NotBlank(message = "refreshToken은 필수입니다")
    val refreshToken: String
)