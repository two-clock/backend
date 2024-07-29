package com.twoclock.gitconnect.domain.page.repository;

import com.twoclock.gitconnect.domain.page.entity.PageViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageViewHistoryRepository extends JpaRepository<PageViewHistory, Long> {
}
