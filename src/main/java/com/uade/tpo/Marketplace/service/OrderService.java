package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Order;
import com.uade.tpo.Marketplace.entity.User;
import com.uade.tpo.Marketplace.repository.OrderRepository;
import com.uade.tpo.Marketplace.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository repo;
    @Autowired
    private UserRepository userRepo;

  @Transactional(readOnly = true)
  public List<Order> findAll(){ return repo.findAll(); }

  @Transactional(readOnly = true)
  public Order findById(Long id){
    return repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
  }

  public Order create(Order o){
    if (o.getUser() == null || o.getUser().getId() == null) {
      throw new IllegalArgumentException("User id is required");
    }
    //  cargar entidad real (no proxy)
    User user = userRepo.findById(o.getUser().getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + o.getUser().getId()));

    o.setUser(user);
    o.setId(null);
    return repo.save(o);
  }

  public Order update(Long id, Order o){
    Order db = findById(id);

    if (o.getTotal() != null) db.setTotal(o.getTotal());
    if (o.getDate()  != null) db.setDate(o.getDate());

    if (o.getUser() != null && o.getUser().getId() != null) {
      // ✅ cargar entidad real (no proxy)
      User user = userRepo.findById(o.getUser().getId())
          .orElseThrow(() -> new IllegalArgumentException("User not found: " + o.getUser().getId()));
      db.setUser(user);
    }

    return repo.save(db);
  }

  public void delete(Long id){
    if(!repo.existsById(id))
      throw new IllegalArgumentException("Order not found: " + id);
    repo.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<Order> findByUserId(Long userId){
    return repo.findByUser_Id(userId);
  }
}