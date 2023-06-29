package org.entando.entando.aps.system.services.pagemodel;

import java.util.List;
import org.entando.entando.aps.system.services.component.IComponentDto;

public interface PageModelServiceUtilizer<T extends IComponentDto> {

    public String getManagerName();

    public List<T> getPageModelUtilizer(String pageModelCode);
}
