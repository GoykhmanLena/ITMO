package ru.lenok.server.connectivity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetAddress;
@Data
@AllArgsConstructor
public class ClientAddress {
        private final InetAddress clientIp;
        private final int clientPort;
}
