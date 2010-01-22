package org.springframework.samples.mvc.ajax.account;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.mapping.support.MappingConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/account")
public class AccountController {

	private ConversionService conversionService;
	
	private Map<Long, Account> accounts = new ConcurrentHashMap<Long, Account>();
	
	private Validator validator;
	
	@Autowired
	public AccountController(ConversionService conversionService, Validator validator) {
		this.conversionService = new MappingConversionService(conversionService);
		this.validator = validator;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {
		model.addAttribute(new Account());
		return "account/createForm";
	}

	@RequestMapping(value="/availability", method=RequestMethod.GET)
	public @ResponseBody AvailabilityStatus getAvailability(@RequestParam String name) {
		for (Account a : accounts.values()) {
			if (a.getName().equals(name)) {
				return AvailabilityStatus.notAvailable(name);
			}
		}
		return AvailabilityStatus.available();
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> create(@RequestBody Map<String, String> accountData, HttpServletResponse response) {
		Account account = this.conversionService.convert(accountData, Account.class);
		Set<ConstraintViolation<Account>> failures = this.validator.validate(account);
		if (!failures.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			Map<String, String> failureMessages = new HashMap<String, String>();
			for (ConstraintViolation<Account> failure : failures) {
				failureMessages.put(failure.getPropertyPath().toString(), failure.getMessage());
			}
			return failureMessages;
		}
		accounts.put(account.assignId(), account);
		return Collections.singletonMap("id", account.getId());			
	}
	
	@RequestMapping(value="{id}", method=RequestMethod.GET)
	public @ResponseBody Account get(@PathVariable Long id) {
		Account account = accounts.get(id);
		if (account == null) {
			throw new ResourceNotFoundException(id);
		}
		return account;
	}
}
