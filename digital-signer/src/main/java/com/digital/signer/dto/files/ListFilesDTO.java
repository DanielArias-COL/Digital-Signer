package com.digital.signer.dto.files;

import com.digital.signer.dto.transversal.ErrorDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@ToString
public class ListFilesDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private List<FileDTO> listFiles;

    private ErrorDTO error;

}
