package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /**
     * 주의
     * BindingResult 는 검증할 대상 바로 다음에 와야한다. 순서가 중요!!!
     * BindingResult 는 Model 에 자동으로 포함됨
     * BindingResult bindingResult 파라미터의 위치는 @ModelAttribute Item item 다음에 와야 한다.
     *
     * BindingResult 2 정리
     * BindingResult 가 없으면 -> 400 오류가 발생하면서 컨트롤러가 호출되지 않고, 오류 페이지로 이동
     * BindingResult 가 있으면 -> 오류 정보( FieldError )를 BindingResult 에 담아서 컨트롤러를 정상 호출
     *
     * BindingResult 에 검증 오류를 적용하는 3가지 방법
     * 1. @ModelAttribute 의 객체에 타입 오류 등으로 바인딩이 실패하는 경우 -> 스프링이 FieldError 생성후 BindingResult 에 넣어줌
     * 2. 개발자가 직접 넣어준다. -> 실습 한 방법
     * 3. Validator 사용
     *
     * BindingResult와 Errors
     * BindingResult 는 인터페이스이고, Errors 인터페이스를 상속받고 있다.
     * Errors 인터페이스는 단순한 오류 저장과 조회 기능을 제공한다.
     * BindingResult 는 여기에 더해서 추가적인 기능들을 제공한다.
     * addError() 도 BindingResult 가 제공하므로 여기서는 BindingResult 를 사용하자.
     * 주로 관례상 BindingResult 를 많이 사용
     */
    //@PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /**
         * 검증 로직
         * objectName : @ModelAttribute 이름
         * field : 오류가 발생한 필드 이름
         * defaultMessage : 오류 기본 메시지
         */
        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }

        /**
         *  특정 필드가 아닌 복합 룰 검증
         *  특정 필드를 넘어서는 오류가 있으면 ObjectError 객체를 생성해서 bindingReulst 에 담아주면 됨
         * objectName : @ModelAttribute 의 이름
         * defaultMessage : 오류 기본 메시지
         */
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        //bindingResult 는 View 에 같이 넘어감
        if(bindingResult.hasErrors()) {
            log.info("errors =  {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /**
         * filed error 파라미터 목록
         * objectName : 오류가 발생한 객체 이름
         * field : 오류 필드
         * rejectedValue : 사용자가 입력한 값(거절된 값)
         * bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값 codes : 메시지 코드
         * arguments : 메시지에서 사용하는 인자
         * defaultMessage : 기본 오류 메시지
         *
         * 사용자의 입력 데이터가 컨트롤러의 @ModelAttribute 에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어렵다.
         * ex) 가격에 숫자가 아닌 문자가 입력된다면 가격은 Integer 타입이므로 문자를 보관할 수 있는 방법이 없다.
         * FieldError 는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공
         *
         * 여기서 rejectedValue 가 바로 오류 발생시 사용자 입력 값을 저장하는 필드다.
         * bindingFailure 는 타입 오류 같은 바인딩이 실패했는지 여부를 적어주면 된다.
         * 여기서는 바인딩이 실패한 것은 아니기 때문에 false 를 사용
         *
         * 스프링의 바인딩 오류 처리
         * 타입 오류로 바인딩에 실패하면 스프링은 FieldError 를 생성하면서 사용자가 입력한 값을 넣어둔다.
         * 그리고 해당 오류를 BindingResult 에 담아서 컨트롤러를 호출한다.
         * 따라서 타입 오류 같은 바인싱 실패시에도 사용자의 오류 메시지를 정상 출력할 수 있다.
         */
        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
                    false, null, null, "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() > 10000) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
                    false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            } }


        if(bindingResult.hasErrors()) {
            log.info("errors =  {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

