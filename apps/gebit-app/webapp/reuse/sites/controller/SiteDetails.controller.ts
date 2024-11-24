import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import JSONModel from "sap/ui/model/json/JSONModel";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Dialog from "sap/m/Dialog";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";
import MessageBox from "sap/m/MessageBox";
import MessageToast from "sap/m/MessageToast";
import Context from "sap/ui/model/odata/v4/Context";
import Button from "sap/m/Button";
/**
 * @namespace ui5.gebit.app.reuse.sites.controller
 */           
export default class SiteDetails extends Controller {

	addUserToSiteDialog:Dialog;
	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "site") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		(this.getView() as any).bindElement("/Tenants("+context+")");
		
	}

	public onEditButton(oEvent:Event) {
		let uiModel = this.getView()?.getModel("uiModel") as JSONModel;
		uiModel.setProperty("/ui/editMode", true);
		(this.byId("deleteSelectedUserButton") as Button).setEnabled(false);
	}

	public onDoneEditButton(oEvent:Event) {
		let uiModel = this.getView()?.getModel("uiModel") as JSONModel;
		uiModel.setProperty("/ui/editMode", false);
	}

	public onUserTableSelectionChange(oEvent: Event) {
		let isSelected = oEvent.getParameter("selected") as boolean;
		(this.byId("deleteSelectedUserButton") as Button).setEnabled(isSelected);
	}

	public onPressRemodeSelectedUserButton (oEvent: Event) {

	}

	public onPressAddUserButton (oEvent: Event) {
		
			let that = this;
			if(this.addUserToSiteDialog == null) {
				this.loadFragment({name:"ui5.gebit.app.reuse.sites.fragment.AddUserToSite", addToDependents: true}).then(function(dialog:any){
					that.addUserToSiteDialog = dialog as Dialog;
					that.addUserToSiteDialog.bindElement({
						path: "/create/kml/territory",
						model:"uiModel"
					});
					that.addUserToSiteDialog.open();
	
					
				}.bind(this));
			} else {
				// (this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/isXmlParsed", false);
				// (this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/territory", {});
				that.addUserToSiteDialog.open();
	
			}
		
	}

	public  closeAddUserToSiteDialog(oEvent: Event) {
		this.getView()?.getModel("uiModel").setProperty("/sites/create", {});
		this.addUserToSiteDialog.close();
	}

	public async addUserToSiteByEmail(oEvent:Event) {
		let object = this.getView()?.getModel("uiModel").getProperty("/sites/create");

		let model = (this.getView()?.getModel() as ODataModel);
		let detailPageContext = this.getView()?.getBindingContext() as Context;
		let context = await model.bindContext("srv.admin.addUserByEmail(...)", detailPageContext);
		context.setParameter("email", object.email);
		context.setParameter("mappingType", object.mappingType);

		context.execute().then(function () {
				MessageToast.show("{i18n>ok}");
				this.getView()?.getModel().refresh();
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
		);
	}
}