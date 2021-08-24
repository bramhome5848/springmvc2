package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    /**
     * HTTP 요청 파라미터는 모두 문자로 처리
     * 요청 파라미터를 자바에서 다른 타입으로 변환해서 사용하고 싶으면
     * 숫자 타입으로 변환하는 과정을 거쳐야 함
     */
    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest request) {
        String data = request.getParameter("data"); //문자 타입으로 조회
        Integer intValue = Integer.valueOf(data); //숫자 타입으로 변경
        System.out.println("intValue = " + intValue);
        return "ok";
    }

    /**
     * 스프링이 제공하는 @RequestParam 을 사용하면 문자 10을 Integer 타입의 숫자 10으로 편리하게 받을 수 있음
     * 이것은 스프링이 중간에서 타입을 변환해주었기 때문
     *
     * @ModelAttribute , @PathVariable 또한 스프링이 중간에 타입을 변환
     * <p>
     * 스프링은 확장 가능한 컨버터 인터페이스를 제공
     * 개발자는 스프링에 추가적인 타입 변환이 필요하면 이 컨버터 인터페이스를 구현해서 등록하면 됨
     */
    @GetMapping("/hello-v2")
    public String helloV2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
    }

    /**
     * 처리 과정
     * @RequestParam 은 @RequestParam 을 처리하는 ArgumentResolver 인
     * RequestParamMethodArgumentResolver 에서 ConversionService 를 사용해서 타입을 변환
     */
    @GetMapping("/ip-port")
    public String ipPort(@RequestParam IpPort ipPort) {
        System.out.println("ipPort IP = " + ipPort.getIp());
        System.out.println("ipPort PORT = " + ipPort.getPort());
        return "ok";
    }
}
