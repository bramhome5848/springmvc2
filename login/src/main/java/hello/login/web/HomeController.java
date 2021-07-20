package hello.login.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    /**
     * 참고
     * 도메인 = 화면, UI, 기술 인프라 등등의 영역은 제외한 시스템이 구현해야 하는 핵심 비즈니스 업무 영역을 말함
     * 향후 web을 다른 기술로 바꾸어도 도메인은 그대로 유지할 수 있어야 한다.
     * 이렇게 하려면 web 은 domain 을 참조하지만 domain 은 web 을 참조하지 않아야 한다.
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}