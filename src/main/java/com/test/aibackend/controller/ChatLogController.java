package com.test.aibackend.controller;

import com.test.aibackend.domain.ChatLog;
import com.test.aibackend.dto.ChatLogRequest;
import com.test.aibackend.dto.ChatLogResponse;
import com.test.aibackend.service.ChatLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 채팅 로그 관련 API를 처리하는 Controller.
 *
 * 기본 URL:
 * - /chat-logs
 *
 * 주요 기능:
 * - 특정 유저의 채팅 로그 목록 조회
 * - 특정 유저의 채팅 로그 목록 조회 + username 포함
 * - 채팅 로그 생성
 */
@RestController
@RequestMapping("/chat-logs")
@RequiredArgsConstructor
public class ChatLogController {

    /**
     * ChatLog 관련 비즈니스 로직을 담당하는 Service.
     *
     * @RequiredArgsConstructor에 의해 생성자 주입됩니다.
     */
    private final ChatLogService chatLogService;

    /**
     * [getId 버전]
     *
     * 특정 userId(PK)에 해당하는 대화 로그를 최신순으로 조회합니다.
     *
     * 기존 RequestParam 방식:
     * - GET /chat-logs?userId=1
     *
     * 현재 PathVariable 방식:
     * - GET /chat-logs/users/1
     *
     * PathVariable을 사용하면 userId가 URL 경로에 포함됩니다.
     * 즉, "1번 유저와 관련된 채팅 로그"라는 의미가 URL에 더 명확하게 드러납니다.
     *
     * fetch join 없이 ChatLog만 조회한 뒤 ChatLogResponse.from()을 사용합니다.
     * 따라서 User 엔티티의 username까지 응답에 담지 않고, username은 null이 될 수 있습니다.
     *
     * @param userId 채팅 로그를 조회할 유저의 PK
     * @return 해당 유저의 채팅 로그 응답 목록
     */
    @GetMapping("/users/{userId}")
    public List<ChatLogResponse> list(@PathVariable Long userId) {
        return chatLogService.findByUserId(userId).stream()
                .map(ChatLogResponse::from)
                .toList();
    }

    /**
     * [getName 버전]
     *
     * 특정 userId(PK)에 해당하는 대화 로그를 최신순으로 조회합니다.
     * 이때 fetch join을 사용해서 ChatLog와 연관된 User도 함께 조회합니다.
     *
     * 기존 RequestParam 방식:
     * - GET /chat-logs/with-user?userId=1
     *
     * 현재 PathVariable 방식:
     * - GET /chat-logs/users/1/with-user
     *
     * fetch join을 사용하면 ChatLog 조회 시점에 User도 함께 로딩됩니다.
     * 따라서 Service의 트랜잭션이 끝난 뒤에도 getUser().getUsername() 접근이 안전합니다.
     *
     * ChatLogResponse.fromWithUsername()을 사용하므로 username까지 응답에 포함됩니다.
     *
     * @param userId 채팅 로그를 조회할 유저의 PK
     * @return username이 포함된 채팅 로그 응답 목록
     */
    @GetMapping("/users/{userId}/with-user")
    public List<ChatLogResponse> listWithUser(@PathVariable Long userId) {
        return chatLogService.findByUserIdWithUser(userId).stream()
                .map(ChatLogResponse::fromWithUsername)
                .toList();
    }

    /**
     * 채팅 로그를 생성합니다.
     *
     * 요청 방식:
     * - POST /chat-logs
     *
     * 요청 Body 예시:
     * {
     *   "userId": 1,
     *   "prompt": "안녕하세요",
     *   "response": "안녕하세요! 무엇을 도와드릴까요?"
     * }
     *
     * @Valid:
     * - ChatLogRequest에 작성된 검증 조건을 실행합니다.
     * - 예: @NotNull, @NotBlank 등
     *
     * @RequestBody:
     * - HTTP 요청 Body의 JSON 데이터를 ChatLogRequest 객체로 변환합니다.
     *
     * ResponseEntity.created(location):
     * - HTTP 상태 코드 201 Created를 반환합니다.
     * - Location 헤더에 새로 생성된 리소스의 URI를 담습니다.
     *
     * @param req 채팅 로그 생성 요청 DTO
     * @return 생성된 채팅 로그 응답 DTO
     */
    @PostMapping
    public ResponseEntity<ChatLogResponse> create(@Valid @RequestBody ChatLogRequest req) {
        ChatLog saved = chatLogService.save(req.userId(), req.prompt(), req.response());

        URI location = URI.create("/chat-logs/" + saved.getId());

        return ResponseEntity
                .created(location)
                .body(ChatLogResponse.from(saved));
    }
}