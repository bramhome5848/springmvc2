package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @NotBlank : 빈값 + 공백만 있는 경우를 허용하지 않는다.
 * @NotNull : null 을 허용하지 않는다.
 * @Range(min = 1000, max = 1000000) : 범위 안의 값이어야 한다.
 * @Max(9999) : 최대 9999까지만 허용한다.
 *
 * 참고
 * javax.validation 으로 시작하면 특정 구현에 관계없이 제공되는 표준 인터페이스이고,
 * org.hibernate.validator 로 시작하면 하이버네이트 validator 구현체를 사용할 때만 제공되는 검증 기능이다.
 * 실무에서 대부분 하이버네이트 validator 를 사용하므로 자유롭게 사용해도 된다.
 *
 * @NotNull, @NotEmpty, @NotBlank
 * @NotNull -> Null 만 허용하지 않음, "" 이나 " " 은 허용,
 * @NotEmpty -> null 과 "" 둘 다 허용하지 않음, " " 은 허용
 * @NotBlank -> null 과 "" 과 " " 모두 허용하지 않음, 3개 중 validation 강도가 높은 것
 *
 * 특정 필드( FieldError )가 아닌 해당 오브젝트 관련 오류( ObjectError ) 처리
 * ->  * 다음과 같이 @ScriptAssert() 를 사용
 * 실제 사용해보면 제약이 많고 복잡하다. 그리고 실무에서는 검증 기능이 해당 객체의 범위를 넘어서는 경우들도 종종 등장하는데,
 * 그런 경우 대응이 어렵다. -> 오브젝트 관련 오류는 자바 코드로 직접 처리하는 것을 권장
 *
 */
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
@Data
public class Item {

    @NotNull(groups = UpdateCheck.class) //수정시에만 적용
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 100000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = SaveCheck.class) //등록시에만 적용
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
