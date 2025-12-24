package com.guilinares.clinicai.clinic.service;

import com.guilinares.clinicai.ai.ClinicalNluService;
import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.ClinicKbEntryEntity;
import com.guilinares.clinicai.clinic.ClinicKbEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicKbAiService {

    private final ClinicKbEntryRepository repo;
    private final ClinicalNluService clinicalNluService;

    public String answer(ClinicEntity clinic, String userQuestion) {

        List<ClinicKbEntryEntity> hits = repo.search(clinic.getId(), userQuestion, 5);

        String kbContext = buildKbContext(hits);

        // Se não achar nada, ainda dá pra mandar vazio pro modelo responder “não tenho info”
        return clinicalNluService.answerFromKb(kbContext, userQuestion);
    }

    private String buildKbContext(List<ClinicKbEntryEntity> hits) {
        if (hits == null || hits.isEmpty()) return "(KB vazio para essa pergunta)";

        StringBuilder sb = new StringBuilder();
        for (ClinicKbEntryEntity e : hits) {
            sb.append("### ").append(e.getTitle()).append(" (topic=").append(e.getTopic()).append(")\n");
            sb.append(e.getContent()).append("\n\n");
        }
        return sb.toString();
    }
}
