package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/admin/station")
@CrossOrigin
public class StationController {

    @Autowired
    private StationService stationService;

    /** 本分站未分配任务单列表（支持日期+类型过滤） */
    @GetMapping("/pending-tasks")
    public Result getPendingTasks(
            @RequestParam Long stationId,
            @RequestParam(required = false) Byte taskType,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd) {
        return stationService.getPendingTasks(stationId, taskType, dateStart, dateEnd);
    }

    /** 任务分配：绑定配送员 */
    @PostMapping("/assign")
    public Result assign(@RequestBody Map<String, Object> body) {
        Long taskId = Long.valueOf(body.get("taskId").toString());
        Long orderId = Long.valueOf(body.get("orderId").toString());
        Long courierId = Long.valueOf(body.get("courierId").toString());
        Long adminId = body.get("adminId") != null ? Long.valueOf(body.get("adminId").toString()) : 0L;
        return stationService.assignCourier(taskId, orderId, courierId, adminId);
    }

    /** 本分站已分配任务单列表（打印、回执用） */
    @GetMapping("/assigned-tasks")
    public Result getAssignedTasks(@RequestParam Long stationId) {
        return stationService.getAssignedTasks(stationId);
    }

    /** 获取打印签收单数据（任务头部信息 + 商品明细） */
    @GetMapping("/print-data")
    public Result getPrintData(@RequestParam Long taskId) {
        return stationService.getPrintData(taskId);
    }

    /** 获取所有配送员列表（role=4） */
    @GetMapping("/couriers")
    public Result getCouriers() {
        return stationService.getCouriers();
    }

    /** 回执录入并结单 */
    @PostMapping("/receipt")
    public Result submitReceipt(@RequestBody Map<String, Object> body) {
        Long taskId = Long.valueOf(body.get("taskId").toString());
        Long orderId = Long.valueOf(body.get("orderId").toString());
        Byte completeStatus = Byte.valueOf(body.get("completeStatus").toString());
        BigDecimal actualAmount = new BigDecimal(body.get("actualAmount").toString());
        String remark = body.get("remark") != null ? body.get("remark").toString() : "";
        return stationService.submitReceipt(taskId, orderId, completeStatus, actualAmount, remark);
    }

    /** 缴款统计查询（GROUP BY 日期，含汇总行） */
    @GetMapping("/payment-stats")
    public Result getPaymentStats(
            @RequestParam Long stationId,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd) {
        return stationService.getPaymentStats(stationId, dateStart, dateEnd);
    }
}