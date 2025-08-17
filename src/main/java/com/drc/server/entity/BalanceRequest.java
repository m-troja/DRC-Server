package com.drc.server.entity;

public record BalanceRequest(String username, BalanceAction balanceAction, Double value) {



}
