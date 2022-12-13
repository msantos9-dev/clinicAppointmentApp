package biz.global77.clinic.service;

import java.util.List;

import biz.global77.clinic.model.Code;

public interface CodeService {

    Code getCodeById(int id);

    void saveCode(Code code);

    List<Code> getAllCode();
    
}
