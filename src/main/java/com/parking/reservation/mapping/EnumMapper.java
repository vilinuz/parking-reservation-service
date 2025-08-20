package com.parking.reservation.mapping;

public class EnumMapper {

    public static <S extends Enum<S>, T extends Enum<T>> T mapEnum(S sourceEnum, Class<T> targetEnumClass) {
        if (sourceEnum == null || targetEnumClass == null) {
            return null;
        }
        try {
            return Enum.valueOf(targetEnumClass, sourceEnum.name());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static <T extends Enum<T>> T mapEnum(String source, Class<T> targetEnumClass) {
        if (source == null || targetEnumClass == null) {
            return null;
        }
        try {
            return Enum.valueOf(targetEnumClass, source);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
