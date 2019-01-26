import { BaseEntity } from './../../shared';

export const enum ManufacturingOrderStatus {
    'OPEN',
    'IN_PROGRESS',
    'COMPLETED',
    'FAILED'
}

export class ManufacturingOrder implements BaseEntity {
    constructor(
        public id?: string,
        public pricePerUnit?: number,
        public typeName?: string,
        public amount?: number,
        public instant?: any,
        public assignee?: string,
        public status?: ManufacturingOrderStatus,
    ) {
    }
}
