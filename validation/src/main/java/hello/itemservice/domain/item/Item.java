package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

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
 */
@Data
public class Item {

    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 100000)
    private Integer price;

    @Max(9999)
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
