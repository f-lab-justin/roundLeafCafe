package com.ypdchurch.roundleafcafe.member.service;

import com.ypdchurch.roundleafcafe.common.exception.CustomApiException;
import com.ypdchurch.roundleafcafe.member.domain.Member;
import com.ypdchurch.roundleafcafe.member.enums.MemberRole;
import com.ypdchurch.roundleafcafe.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.ypdchurch.roundleafcafe.member.service.MemberService.JoinRequestDto;
import static com.ypdchurch.roundleafcafe.member.service.MemberService.JoinResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 테스트")
    public void registerMemberTest() throws IllegalAccessException {
        //given
        JoinRequestDto joinRequestDto = JoinRequestDto.builder()
                .name("tom")
                .password("1234")
                .email("tom@gmail.com")
                .phoneNumber("01012345678")
                .build();

        //stub1
        when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());

        //stub2
        Member member = Member.builder()
                .id(1L)
                .name("tom")
                .password("1234")
                .email("tom@gmail.com")
                .phoneNumber("01012345678")
                .role(MemberRole.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        when(memberRepository.save(any())).thenReturn(member);

        //when
        JoinResponseDto joinResponseDto = memberService.registerMember(joinRequestDto);
        //then
        assertThat(joinResponseDto.getEmail()).isEqualTo("tom@gmail.com");
    }

    @Test
    @DisplayName("회원 가입 중복체크 테스트 ")
    public void registerMemberDuplicationCheckTest() throws IllegalAccessException {
        //given
        JoinRequestDto joinRequestDto = JoinRequestDto.builder()
                .name("tom")
                .password("1234")
                .email("tom@gmail.com")
                .phoneNumber("01012345678")
                .build();

        //stub1
        when(memberRepository.findByEmail(any())).thenReturn(
                Optional.of(Member.builder()
                        .id(1L)
                        .name("tom")
                        .password("1234")
                        .email("tom@gmail.com")
                        .phoneNumber("01012345678")
                        .build()));
        //when
        //then
        assertThatThrownBy(() ->
                memberService.registerMember(joinRequestDto))
                .isInstanceOf(CustomApiException.class)
                .hasMessage("등록된 이메일이 존재합니다.");
    }


}