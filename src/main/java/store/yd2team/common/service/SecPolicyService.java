package store.yd2team.common.service;

public interface SecPolicyService {

	SecPolicyVO getByVendIdOrDefault(String vendId);

    SecPolicyVO saveForVend(String vendId, String empId, SecPolicyVO reqVo);
    
    boolean existsForVend(String vendId);
}
