import ResourceBundle from "sap/base/i18n/ResourceBundle";
import Title from "sap/m/Title";
import ODataListBinding, { ODataListBinding$ChangeEvent } from "sap/ui/model/odata/v4/ODataListBinding";


export default class TableTitleSetter {
    constructor(private bindingContext: ODataListBinding, private title: Title, private resourceBundle: ResourceBundle, private bundleTextKey: string) {
        this.bindingContext.attachEvent("change", this.onChangeEvent.bind(this));
    }

    private onChangeEvent(oEvent:ODataListBinding$ChangeEvent) {
        let count = this.bindingContext.getLength();                    
        let titleString = this.resourceBundle.getText(this.bundleTextKey, [count]);
        this.title.setText(titleString);
    }
}