import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../../public/Component";
import UIComponent from "sap/ui/core/UIComponent";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import View from "sap/ui/core/mvc/View";
import { URLHelper } from "sap/m/library";
import Control from "sap/ui/core/Control";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";
import Table from "sap/m/Table";
import Context from "sap/ui/model/Context";
import ColumnListItem from "sap/m/ColumnListItem";
import Text from "sap/m/Text";
import Dialog from "sap/m/Dialog";
import JSONModel from "sap/ui/model/json/JSONModel";
import MessageToast from "sap/m/MessageToast";
import Button from "sap/m/Button";

/**
 * @namespace ui5.gebit.app.reuse.sites.controller
 */           
export default class SitesWorklist extends Controller {

	oAddSiteDialog: Dialog;
	oSelectedColumnListItem: ColumnListItem
	public onInit() : void {
		// apply content density mode to root view
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}

        let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

    public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "sites") {
			console.log("refreshWorklist");
		}
      
	}

	public onFilterSelect (oEvent:Event) {
		let sKey = oEvent.getParameter("key");
		let table = this.getView()?.byId("sitesWorklist") as Table;
		

		let cli = new ColumnListItem({
			cells: [
				new Text({
					text: "{name}"
				}),
				new Text({
					text: "{description}"
				}),
				new Text({
					text: "{type}"
				})
			]
		});
		table.bindItems({
			path: "/" + sKey,
			template: cli
		})
			
		
	}

	public onPressListItem(oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("site",{
			"id" : oBindingContext?.getProperty("ID")
		})
	}

	public onOpenAddSiteDialog(oEven:Event) {
		let that = this;
		if(this.oAddSiteDialog == null) {
			this.loadFragment({name:"ui5.gebit.app.reuse.sites.fragment.CreateSite", addToDependents: true}).then(function(dialog:any){
				that.oAddSiteDialog = dialog as Dialog;
				that.oAddSiteDialog.bindElement({
					path: "/sites/createSite",
					model:"uiModel"
				});
				that.oAddSiteDialog.open();

				
			}.bind(this));
		} else {
			that.oAddSiteDialog.open();

		}
	}

	public onCloseAddSiteDialog(oEven: Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/sites/createSite", {});
		this.oAddSiteDialog.close();
	}



	public async onPressCreateSite(pEvent:Event) {
		let object = this.getView()?.getModel("uiModel").getProperty("/sites/createSite");

		let model = (this.getView()?.getModel() as ODataModel);
		let context = await model.bindContext("/createSite(...)");
		context.setParameter("name", object.name);
		context.setParameter("description", object.description);


		context.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			this.getView()?.getModel().refresh();
			this.oAddSiteDialog.close();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
			this.oAddSiteDialog.close();
		});

	}

	public onUserTableSelectionChange(oEvent:Event) {
		this.oSelectedColumnListItem = oEvent.getParameter("listItem");
		let isSelected = oEvent.getParameter("selected") as boolean;
		(this.byId("deleteSelectedSiteButton") as Button).setEnabled(isSelected);
	}

	public async onPressRemoveSite(pEvent: Event) {
		let model = (this.getView()?.getModel() as ODataModel);

		let actionContext = await model.bindContext("srv.admin.removeSite(...)", this.oSelectedColumnListItem.getBindingContext());
		
		actionContext.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			this.getView()?.getModel().refresh();
			this.oAddSiteDialog.close();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
			this.oAddSiteDialog.close();
		});
	}

	public onCancelCreateSite(oEvent: Event) {
		this.oAddSiteDialog.close();
	}
}