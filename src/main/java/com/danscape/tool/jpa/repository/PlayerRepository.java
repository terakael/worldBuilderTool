package com.danscape.tool.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danscape.tool.jpa.entity.PlayerEntity;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Integer>{

}
