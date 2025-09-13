package com.lcsk42.starter.database.model.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasePageResp<V> {
    /**
     * 当前页码
     */
    private long current;

    /**
     * 页面大小
     */
    @Builder.Default
    private long size = 10L;

    /**
     * 总数量
     */
    @Builder.Default
    private long total = 0L;

    /**
     * 查询出的记录
     */
    @SuppressWarnings("squid:S1948")
    @Builder.Default
    private List<V> records = List.of();

    public BasePageResp(long total, List<V> records) {
        this.total = total;
        this.records = records;
    }

    public static <V> BasePageResp<V> of(long current, long size) {
        return BasePageResp.<V>builder()
                .current(current)
                .size(size)
                .build();
    }

    public static <V> BasePageResp<V> of(long current, long size, long total, List<V> records) {
        return BasePageResp.<V>builder()
                .current(current)
                .size(size)
                .total(total)
                .records(records)
                .build();
    }


    public BasePageResp(long current, long size) {
        this(current, size, 0);
    }

    public BasePageResp(long current, long size, long total) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
    }

    public <R> BasePageResp<R> convert(Function<? super V, ? extends R> mapper) {
        List<? extends R> mapped = this.getRecords().stream()
                .map(mapper)
                .toList();
        List<R> collect = new ArrayList<>(mapped);
        return BasePageResp.<R>builder()
                .current(this.current)
                .size(this.size)
                .total(this.total)
                .records(collect)
                .build();
    }
}
