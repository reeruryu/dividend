package com.example.dividend.persist;

import com.example.dividend.persist.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

	Optional<MemberEntity> findByUsername(String username);

	boolean existsByUsername(String username);

}

