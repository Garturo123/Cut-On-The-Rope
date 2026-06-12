/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ctr.entity;

import Usuarios.Menu;
import ctr.Entity;
import ctr.Scene;

/**
 *
 * @author gaat1
 */
public class AmigosListEntity extends Entity{
    private Menu menus;

    public AmigosListEntity(Scene scene, Menu menus) {
        super(scene);
        this.menus = menus;
    }
    
}
