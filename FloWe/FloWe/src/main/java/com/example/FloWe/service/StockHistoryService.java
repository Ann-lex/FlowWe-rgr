package com.example.FloWe.service;

import com.example.FloWe.model.Product;
import com.example.FloWe.model.StockHistory;
import com.example.FloWe.repository.StockHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockHistoryService {

    private static final String REASON_PRODUCT_CREATED = "Товар создан";
    private static final String REASON_SELLER_UPDATE = "Остаток изменён продавцом";
    private static final String REASON_ORDER_PURCHASE = "Товар куплен";
    private static final String REASON_PRODUCT_DELETED = "Товар удалён";
    private static final String REASON_PRODUCT_DELETED_BY_ADMIN = "Товар удалён администратором";

    private final StockHistoryRepository stockHistoryRepository;

    public StockHistoryService(StockHistoryRepository stockHistoryRepository) {
        this.stockHistoryRepository = stockHistoryRepository;
    }

    public void logProductCreated(Product product, Long sellerId) {
        if (product == null) {
            return;
        }

        int quantity = product.getQuantity() != null ? product.getQuantity() : 0;

        StockHistory history = new StockHistory();
        history.setProductId(product.getId());
        history.setProductName(product.getName());
        history.setSellerId(sellerId);
        history.setUserId(sellerId);
        history.setOldQuantity(0);
        history.setNewQuantity(quantity);
        history.setChangeAmount(quantity);
        history.setReason(REASON_PRODUCT_CREATED);

        stockHistoryRepository.save(history);
    }

    public void logSellerUpdate(Product oldProduct, Product newProduct, Long sellerId) {
        if (oldProduct == null || newProduct == null) {
            return;
        }

        int oldQuantity = oldProduct.getQuantity() != null ? oldProduct.getQuantity() : 0;
        int newQuantity = newProduct.getQuantity() != null ? newProduct.getQuantity() : 0;

        if (oldQuantity == newQuantity) {
            return;
        }

        StockHistory history = new StockHistory();
        history.setProductId(oldProduct.getId());
        history.setProductName(newProduct.getName());
        history.setSellerId(sellerId);
        history.setUserId(sellerId);
        history.setOldQuantity(oldQuantity);
        history.setNewQuantity(newQuantity);
        history.setChangeAmount(newQuantity - oldQuantity);
        history.setReason(REASON_SELLER_UPDATE);

        stockHistoryRepository.save(history);
    }

    public void logOrderPurchase(Product product, Long buyerId, Long orderId, Integer purchasedQuantity) {
        if (product == null || purchasedQuantity == null) {
            return;
        }

        int oldQuantity = product.getQuantity() != null ? product.getQuantity() : 0;
        int newQuantity = oldQuantity - purchasedQuantity;

        StockHistory history = new StockHistory();
        history.setProductId(product.getId());
        history.setProductName(product.getName());
        history.setSellerId(product.getSellerId());
        history.setUserId(buyerId);
        history.setOrderId(orderId);
        history.setOldQuantity(oldQuantity);
        history.setNewQuantity(newQuantity);
        history.setChangeAmount(-purchasedQuantity);
        history.setReason(REASON_ORDER_PURCHASE);

        stockHistoryRepository.save(history);
    }

    public void logProductDeleted(Product product, Long sellerId) {
        if (product == null) {
            return;
        }

        int oldQuantity = product.getQuantity() != null ? product.getQuantity() : 0;

        StockHistory history = new StockHistory();
        history.setProductId(product.getId());
        history.setProductName(product.getName());
        history.setSellerId(sellerId);
        history.setUserId(sellerId);
        history.setOldQuantity(oldQuantity);
        history.setNewQuantity(0);
        history.setChangeAmount(-oldQuantity);
        history.setReason(REASON_PRODUCT_DELETED);

        stockHistoryRepository.save(history);
    }

    public void logProductDeletedByAdmin(Product product) {
        if (product == null) {
            return;
        }

        int oldQuantity = product.getQuantity() != null ? product.getQuantity() : 0;

        StockHistory history = new StockHistory();
        history.setProductId(product.getId());
        history.setProductName(product.getName());
        history.setSellerId(product.getSellerId());
        history.setUserId(null);
        history.setOldQuantity(oldQuantity);
        history.setNewQuantity(0);
        history.setChangeAmount(-oldQuantity);
        history.setReason(REASON_PRODUCT_DELETED_BY_ADMIN);

        stockHistoryRepository.save(history);
    }

    public List<StockHistory> findAll() {
        return stockHistoryRepository.findAll();
    }

    public List<StockHistory> findBySellerId(Long sellerId) {
        return stockHistoryRepository.findBySellerId(sellerId);
    }
}