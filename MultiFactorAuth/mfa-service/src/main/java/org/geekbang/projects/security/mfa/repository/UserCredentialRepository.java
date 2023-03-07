package org.geekbang.projects.security.mfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.geekbang.projects.security.mfa.model.UserCredential;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer> {

	UserCredential findUserCredentialByUsername(String username);
}
