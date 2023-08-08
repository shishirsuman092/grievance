package org.upsmf.grievance.model.request;

import lombok.*;
import org.upsmf.grievance.model.enums.Roles;
import org.upsmf.grievance.model.enums.StatusType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class SearchRequest {

    private StatusType statusType;

    private Roles roles;

}
