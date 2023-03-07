package org.geekbang.projects.security.mfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.geekbang.projects.security.mfa.model.SecurityCode;

public interface SecurityCodeRepository extends JpaRepository<SecurityCode, Integer> {

	SecurityCode findSecurityCodeByUsername(String username);
}
