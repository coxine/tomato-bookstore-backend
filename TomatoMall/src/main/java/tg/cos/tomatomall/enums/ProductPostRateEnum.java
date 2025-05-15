package tg.cos.tomatomall.enums;

import lombok.Getter;

@Getter
public enum ProductPostRateEnum {
    FINDFAIL(-1), OUTRANGE(-2), REPEATED(-3);

    private final float value;

    ProductPostRateEnum(float value) {
        this.value = value;
    }

}
