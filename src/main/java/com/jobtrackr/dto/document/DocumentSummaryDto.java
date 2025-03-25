package com.jobtrackr.dto.document;

import java.util.UUID;

public class DocumentSummaryDto {
    private UUID id;
    private String file_name;

    public DocumentSummaryDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}
