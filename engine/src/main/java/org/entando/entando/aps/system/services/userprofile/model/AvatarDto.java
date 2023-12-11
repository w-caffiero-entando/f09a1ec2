package org.entando.entando.aps.system.services.userprofile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvatarDto {

    boolean protectedFolder;
    String currentPath;
    String filename;
    byte[] base64;
    String prevPath;

}
