package fr.ans.psc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SimpleFormController {

	@GetMapping("/secure/patient/form")
	public String getSimpleFormPage() {
		log.debug("SimpleFormController ..");
		return "form-page";
	}
}
