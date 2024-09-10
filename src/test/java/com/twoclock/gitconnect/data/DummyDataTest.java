package com.twoclock.gitconnect.data;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootTest
class DummyDataTest {

    private static final int MEMBER_DATA_SiZE = 100;
    private static final int POST_DATA_SIZE = 100_000;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    Faker faker = new Faker(new Locale("ko"));

    @RepeatedTest(100)
    void dummyData() {
        List<Member> members = new ArrayList<>();
        List<Board> boards = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < MEMBER_DATA_SiZE; i++) {
            String login = faker.internet().uuid().substring(0, 8);
            String githubId = faker.internet().uuid().substring(0, 8);
            String avatarUrl = faker.avatar().image();
            String name = faker.name().fullName().replace(" ", "");

            Member member = new Member(login, githubId, avatarUrl, name, Role.ROLE_USER);
            members.add(member);
        }
        memberRepository.saveAll(members);

        for (int i = 0; i < POST_DATA_SIZE; i++) {
            Member member = members.get(faker.number().numberBetween(0, MEMBER_DATA_SiZE - 1));
            String title = faker.lorem().sentence();
            String content = faker.lorem().paragraph();
            Board board = new Board(member, member.getName(), Category.BD1, title, content);
            boards.add(board);
        }
        bulkInsertBoards(boards);

        long endTime = System.currentTimeMillis();
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");
    }

    private void bulkInsertBoards(List<Board> boards) {
        String sql = "INSERT INTO board (member_id, nickname, category, title, content, is_view, view_count, created_date_time) " +
                "VALUES (?, ?, ?, ?, ?, true, 0, now())";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Board board = boards.get(i);
                ps.setLong(1, board.getMember().getId());
                ps.setString(2, board.getNickname());
                ps.setString(3, board.getCategory().name());
                ps.setString(4, board.getTitle());
                ps.setString(5, board.getContent());
            }

            @Override
            public int getBatchSize() {
                return boards.size();
            }
        });
    }
}