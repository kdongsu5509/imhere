package com.kdongsu5509.imhere.notification.adapter.out

import com.kdongsu5509.imhere.auth.adapter.out.persistence.UserJpaEntity
import jakarta.persistence.*

@Entity
@Table(name = "fcm_token")
class FcmTokenEntity(
    @Column(nullable = false)
    var token: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    val user: UserJpaEntity
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun updateToken(newToken: String) {
        this.token = newToken
    }
}