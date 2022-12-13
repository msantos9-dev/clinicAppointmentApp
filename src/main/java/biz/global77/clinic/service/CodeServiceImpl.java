package biz.global77.clinic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import biz.global77.clinic.model.Code;
import biz.global77.clinic.repository.CodeRepository;

@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
    CodeRepository codeRepo;

    @Override
    public Code getCodeById(int id) {
        // TODO Auto-generated method stub
        Optional<Code> optional = codeRepo.findById(id);
		Code code = null;
		if (optional.isPresent()) {
			code = optional.get();
		} else {
			throw new RuntimeException(" Code not found for id :: " + id);
		}
		return code;
    }

    @Override
    public void saveCode(Code code) {
        // TODO Auto-generated method stub
        this.codeRepo.save(code);
    }

    @Override
    public List<Code> getAllCode() {
        // TODO Auto-generated method stub
        return codeRepo.findAll();
    }
    
}
