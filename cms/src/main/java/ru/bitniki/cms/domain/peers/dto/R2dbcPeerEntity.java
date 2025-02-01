package ru.bitniki.cms.domain.peers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@AllArgsConstructor
@ToString
@Table(name = "peers")
public class R2dbcPeerEntity {
    @Id
    private Long id;
    private String name;
    private String peerStatus;
    private Long ownerId;
    private Long hostId;
}
