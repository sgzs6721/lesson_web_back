package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.PaymentRecordService;
import com.lesson.vo.request.PaymentRecordQueryRequest;
import com.lesson.vo.response.PaymentRecordListVO;
import com.lesson.vo.response.PaymentRecordStatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/record")
@Tag(name = "缴费记录", description = "缴费记录及统计接口")
@RequiredArgsConstructor
public class PaymentRecordController {

    private final PaymentRecordService paymentRecordService;

    @GetMapping("/list")
    @Operation(summary = "缴费记录列表", description = "分页查询缴费记录")
    public Result<PaymentRecordListVO> list(@ModelAttribute PaymentRecordQueryRequest request) {
        return Result.success(paymentRecordService.listPaymentRecords(request));
    }

    @PostMapping("/stat")
    @Operation(summary = "缴费统计", description = "统计缴费次数、缴费金额、退费次数、退费金额")
    public Result<PaymentRecordStatVO> stat(@RequestBody PaymentRecordQueryRequest request) {
        return Result.success(paymentRecordService.statPaymentRecords(request));
    }
} 