package org.gty.demo.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public final class JsonUtils {

    private static class JsonUtilsHolder {
        private static final Gson gson = SpringBeanUtils.getBean(Gson.class);
    }

    private static Gson gson() {
        return JsonUtilsHolder.gson;
    }

    @Nonnull
    public static <T> String toJson(@Nonnull T obj) {
        Objects.requireNonNull(obj, "obj must not be null");

        return gson().toJson(obj);
    }

    @Nonnull
    public static <T> Optional<T> fromJson(@Nonnull String json, @Nonnull Class<T> clazz) {
        Objects.requireNonNull(clazz, "obj must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        return Optional.ofNullable(gson().fromJson(StringEscapeUtils.unescapeJson(json), clazz));
    }

    @Nonnull
    public static <T> Optional<T> fromJson(@Nonnull String json, @Nonnull TypeToken<T> typeToken) {
        Objects.requireNonNull(json, "obj must not be null");
        Objects.requireNonNull(typeToken, "clazz must not be null");

        return Optional.ofNullable(gson().fromJson(StringEscapeUtils.unescapeJson(json), typeToken.getType()));
    }

    private JsonUtils() {
    }
}
