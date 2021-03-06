"""Add Messages. Update users

Revision ID: b0652f75aba8
Revises: 
Create Date: 2020-03-19 20:22:14.086907

"""
from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = 'b0652f75aba8'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('chats', sa.Column('created_date', sa.DateTime(), nullable=True))
    op.add_column('users', sa.Column('republic', sa.String(), nullable=True))
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('users', 'republic')
    op.drop_column('chats', 'created_date')
    # ### end Alembic commands ###
