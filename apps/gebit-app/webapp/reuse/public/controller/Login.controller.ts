import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../../../Component";
import UIComponent from "sap/ui/core/UIComponent";
import { URLHelper } from "sap/m/library";
import Event from "sap/ui/base/Event";
import Input from "sap/m/Input";

/**
 * @namespace ui5.gebit.app.reuse.public.controller
 */
export default class Login extends Controller {

	public onInit() : void {
	}

    public login(oEvent:any) {
        
        let login = this.getView()?.byId("login_loginInput");
        let password = this.getView()?.byId("login_passwordInput");
        let data = {
            "login":login.getProperty("value"),
            "password":password.getProperty("value")
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
                localStorage.removeItem('gebitAccessToken');
                localStorage.setItem('gebitAccessToken', data.accessToken);

                that.getView()?.setBusy(true);
                setTimeout(()=> {
                    URLHelper.redirect("#");
                    that.getView()?.setBusy(false);
                    window.location.reload();
                }, 1500);
                
            }.bind(this),
            error: function(err) {
                if(err.status == 424) {
                    URLHelper.redirect("#/welcome/activate/tenant/" + err.responseJSON.tenant + "/user/" + err.responseJSON.userId);
                } else if(err.status == 401) {
                    MessageBox.error(this.getView().getModel("i18n").getProperty("invalidCredentials"));
                    this.getView()?.byId("login_passwordInput")?.setValue("");
                }
                

            }.bind(this)
        });
    }

    public toToRegistration(oEvent:any) {
        (this.getOwnerComponent() as UIComponent).getRouter().navTo("registration");
    }

    public onLiveChange(oEvent: Event) {

        let input = oEvent.getSource() as Input;

        input.setValue(input.getValue().toLowerCase());
    }
    public toForgotPassword(oEvent:any) {
		((this.getOwnerComponent() as UIComponent)).getRouter().navTo("forgotPassword");
    }
    public getHelp(oEvent:any) {
        let resourceBundle = this.getView().getModel("i18n").getResourceBundle();
        let getHelpEmail = resourceBundle.getText("");
        let getHelpSubject = resourceBundle.getText("getHelpSubject");
        let getHelpText = resourceBundle.getText("getHelpText");
        URLHelper.triggerEmail(getHelpEmail, getHelpSubject, getHelpText);
    }
}