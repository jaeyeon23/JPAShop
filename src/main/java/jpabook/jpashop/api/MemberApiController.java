package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> findMemberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result findMemberV2() {
        List<Member> memberList = memberService.findMembers();
        List<MemberDTO> memberDTOList = memberList.stream()
                .map(member -> new MemberDTO(member.getName()))
                .collect(Collectors.toList());

        return new Result(memberDTOList, memberDTOList.size());
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
        private int count;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/member/{id}")
    public ModifyMemberResponse modifyMemberV2(
            @PathVariable Long id,
            @RequestBody @Valid ModifyMemberRequest request
    ) {
        Member member = new Member();
        member.setName(request.getName());

        memberService.modify(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new ModifyMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class ModifyMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class ModifyMemberRequest {
        private String name;
    }
}
