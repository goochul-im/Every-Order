package com.everyorder.security.oauth

import com.everyorder.domain.member.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomUserDetails(
    private val member: Member,
    private var attributes: Map<String, Any>? = null
) : UserDetails, OAuth2User {

    override fun getAttributes(): Map<String, Any>? {
        return attributes
    }

    override fun getName(): String {
        return member.socialId
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(member.role.name))
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return member.id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
