package br.com.caelum.contas.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

// Cuidado para não importar o errado, pq se for o errado não funciona
// me ferrei com isso
import org.springframework.web.servlet.ModelAndView;

import br.com.caelum.contas.dao.ContaDAO;
import br.com.caelum.contas.modelo.Conta;

@Controller
public class ContaController {

	private ContaDAO dao;
	
	@Autowired
	public ContaController(ContaDAO dao) {
		
		this.dao = dao;
	}
	
	@RequestMapping("/formulario")
	public String formulario(){
		
		return "formulario";
	}
	
	@RequestMapping("/adicionaConta")
	public String adiciona(@Valid Conta conta, BindingResult result){ 
		
		if(result.hasErrors()) {
			
			return "formulario";
		}
		
		System.out.println("Conta adicionada é: "+ conta.getDescricao());

		dao.adiciona(conta);
		
		return "conta/conta_adicionada_com_sucesso";
	}
	
	// Pode ser feito dessa forma
	//@RequestMapping(value="/adicionaConta")
	//public String adiciona(@Valid Conta tarefa, BindingResult result) {
	//   if (result.hasFieldErrors("descricao")) {
	//      return "tarefa/formulario";
	//  }
	//  dao.adiciona(tarefa);
	//  return "redirect:/tarefa/lista";
	//}
	
	@RequestMapping("/listaDeContas")
	public ModelAndView lista() {
		
		List<Conta> contas = dao.lista();
		
		ModelAndView mv = new ModelAndView("conta/lista");
		
		mv.addObject("todasContas", contas);
		
		return mv;
	}
	
	// A listagem podem ser feita dessa forma também
	//@RequestMapping("/listaContas")
	//public String lista(Model mv) {
	//  ContaDao dao = new ContaDao();
	//  List<Conta> contas = dao.lista();
	//mv.addAttribute("contas", contas);
	//return "conta/lista";
	//}
	
	@RequestMapping("/pagaConta")
	public void pagarConta(Conta conta, HttpServletResponse response) {

		dao.paga(conta.getId());
		
		// Status code
		response.setStatus(200);
	}
	
	@RequestMapping("/removeConta")
	public String removerConta(Conta conta) {

		dao.remove(conta);
		
		//redireciona no lado do servidor
		//return "forward:listaDeContas";
		
		//redireciona no lado do cliente
		return "redirect:listaDeContas";
	}
}