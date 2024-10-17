import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../../../Component";
import UIComponent from "sap/ui/core/UIComponent";
import { URLHelper } from "sap/m/library";

/**
 * @namespace ui5.gebit.app.reuse.public.controller
 */
export default class Login extends Controller {

	public onInit() : void {
	}

    public login(oEvent:any) {
        let login = this.getView()?.byId("login_loginInput")?.getProperty("value");
        let password = this.getView()?.byId("login_passwordInput")?.getProperty("value");
        let data = {
            "login":login,
            "password":password
        };
        
        let that = this;
        $.ajax({
            method:"POST",
            crossDomain: true,
            dataType: "json",
            contentType: "application/json",
            url: "/api/auth/login",
            data: JSON.stringify(data),
            
            success: function(data:any) {
                localStorage.setItem('accessToken', data.accessToken);
                setTimeout(()=> {
                    // (that.getOwnerComponent() as UIComponent).getRouter().navTo("home");
                    URLHelper.redirect("#");
                }, 500);
                
            },
            error: function(err) {
                console.log(err.status);
                console.log(err.statusText);

            }
        });
    }

    public toToRegistration(oEvent:any) {
        (this.getOwnerComponent() as UIComponent).getRouter().navTo("registration");
    }
}