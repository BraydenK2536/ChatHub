package com.ChatHub.chathub_backend.repository;

import com.ChatHub.chathub_backend.entity.ChatHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistoryEntity,Long> {
    //历史消息的仓库接口
    List<ChatHistoryEntity> findByTimeAfter(LocalDateTime startTime);
}
