package com.insurance.security;

import com.insurance.model.Agent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
public class AgentUserDetails implements UserDetails {
    private final Integer agentId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * الكونستركتور الأساسي: يبني الـ UserDetails من كائن Agent ويعطيه صلاحية واحدة
     */
    public AgentUserDetails(Agent agent) {
        this.agentId     = agent.getAgentId();
        this.email       = agent.getEmail();
        this.password    = agent.getPassword();
        this.authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + agent.getRole().toUpperCase())
        );
    }

    /**
     * كونستركتور بديل: يسمح بتمرير مجموعة صلاحيات مخصصة
     */
    public AgentUserDetails(Agent agent, Collection<? extends GrantedAuthority> authorities) {
        this.agentId     = agent.getAgentId();
        this.email       = agent.getEmail();
        this.password    = agent.getPassword();
        this.authorities = authorities;
    }

    public Integer getAgentId() {
        return agentId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
