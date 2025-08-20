import Button, { Button$PressEvent } from "sap/m/Button";
import { ListItemBase$PressEvent } from "sap/m/ListItemBase";
import Table from "sap/m/Table";
import Controller from "sap/ui/core/mvc/Controller";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import UIComponent from "sap/ui/core/UIComponent";
import JSONModel from "sap/ui/model/json/JSONModel";
import Context from "sap/ui/model/odata/v4/Context";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";

/**
 * @namespace ui5.gebit.app.reuse.admin.controller
 */           
export default class UserDetail extends Controller {

	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "user") {
			this.matched(oEvent.getParameter("arguments").userId);
		}
	}

	public matched(context:string) {
		(this.getView() as any).bindElement("/Users("+context+")");
		
	}
   
	public onEditButton(oEvent: Button$PressEvent) {
		let uiModel = oEvent.getSource().getModel("uiModel") as JSONModel;
		if (uiModel) {
			uiModel.setProperty("/ui/editMode", true);
		}
	}

	public onDisplayMode(oEvent: Button$PressEvent) {
		let uiModel = oEvent.getSource().getModel("uiModel") as JSONModel;
		if (uiModel) {
			uiModel.setProperty("/ui/editMode", false);
		}
	}

	public async onDeleteSelectedActivation(oEvent: Button$PressEvent) {
		let oTable = this.byId("activationsTable") as Table;

		let oSelectedItem = oTable.getSelectedItem();
		if (oSelectedItem) {
			let oContext = oSelectedItem.getBindingContext() as Context;
			let oListContext = oTable.getBindingContext() as Context;
			if (oContext) {
				let oModel = oContext.getModel() as ODataModel;
				await oModel.delete(oContext.getPath());
				oListContext.refresh();
				oTable.removeSelections(true);
				
			}
		}
	}

	public async onDeleteSelectedMapping(oEvent: Button$PressEvent) {
		let oTable = this.byId("mappingsTable") as Table;

		let oSelectedItem = oTable.getSelectedItem();
		if (oSelectedItem) {
			let oContext = oSelectedItem.getBindingContext() as Context;
			let oListContext = oTable.getBindingContext() as Context;
			if (oContext) {
				let oModel = oContext.getModel() as ODataModel;
				await oModel.delete(oContext.getPath());
				oListContext.refresh();
				oTable.removeSelections(true);
				
			}
		}
	}

}