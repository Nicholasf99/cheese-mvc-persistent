package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private
    MenuDao menuDao;

    @Autowired
    private
    CheeseDao cheeseDao;


    public MenuController() {
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        model.addAttribute("title", menu.getName());

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);

        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("form", addMenuItemForm);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors, @RequestParam int menuId, @RequestParam int cheeseId) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu/add-item";
        }

        Menu menu = menuDao.findOne(menuId);
        Cheese cheese = cheeseDao.findOne(cheeseId);
        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:/menu/view/" + menu.getId();
    }


    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam int[] cheeseIds) {

        for (int cheeseId : cheeseIds) {
            cheeseDao.deleteById(cheeseId);
        }
        return "redirect:";
    }
}